use futures::StreamExt;
use once_cell::sync::Lazy;
use std::convert::TryFrom;
use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::time::Duration;
use tokio::runtime::Runtime;
use tokio::sync::mpsc;

use tokio_stream::wrappers::ReceiverStream;

static RUNTIME: Lazy<Runtime> =
    Lazy::new(|| Runtime::new().expect("Failed to initialize Tokio runtime"));

const CONCURRENT_REQUESTS: usize = 16;

#[no_mangle]
pub extern "C" fn check_links_batch(input_ptr: *const c_char) -> *mut c_char {
    if input_ptr.is_null() {
        return std::ptr::null_mut();
    }

    let input_str = match unsafe { CStr::from_ptr(input_ptr) }.to_str() {
        Ok(s) => s,
        Err(_) => return std::ptr::null_mut(),
    };

    let mut urls = Vec::new();
    for line in input_str.lines() {
        let trimmed = line.trim();
        if !trimmed.is_empty() {
            urls.push(trimmed.to_string());
        }
    }

    let output_string = RUNTIME.block_on(async {
        let (send_req, recv_req) = mpsc::channel(CONCURRENT_REQUESTS);
        let (send_resp, mut recv_resp) = mpsc::channel(CONCURRENT_REQUESTS);

        tokio::spawn(async move {
            for url in urls {
                if let Ok(request) = lychee_lib::Request::try_from(url.as_str()) {
                    let _ = send_req.send((url, request)).await;
                }
            }
        });

        let client = match lychee_lib::ClientBuilder::builder()
            .user_agent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .timeout(Duration::from_secs(5))
            .method(reqwest::Method::GET)
            .build()
            .client()
        {
            Ok(c) => c,
            Err(_) => return "ERROR: Failed to create client".to_string(),
        };

        tokio::spawn(async move {
            ReceiverStream::new(recv_req)
                .for_each_concurrent(CONCURRENT_REQUESTS, |(url, req)| {
                    let client = client.clone();
                    let send_resp = send_resp.clone();
                    async move {
                        match client.check(req).await {
                            Ok(resp) => {
                                let _ = send_resp.send((url, Some(resp))).await;
                            }
                            Err(_err) => {
                                let _ = send_resp.send((url, None)).await;
                            }
                        }
                    }
                })
                .await;
        });
        let mut out = String::new();
        while let Some((url, response_opt)) = recv_resp.recv().await {
            match response_opt {
                Some(response) => {
                    if !response.status().is_success() {
                        out.push_str(&format!("{}\n", url));
                    }
                }
                None => {
                    out.push_str(&format!("{}\n", url));
                }
            }
        }
        out
    });

    match CString::new(output_string) {
        Ok(c_str) => c_str.into_raw(),
        Err(_) => std::ptr::null_mut(),
    }
}

#[no_mangle]
pub extern "C" fn free_rust_string(ptr: *mut c_char) {
    if !ptr.is_null() {
        unsafe {
            let _ = CString::from_raw(ptr);
        }
    }
}
