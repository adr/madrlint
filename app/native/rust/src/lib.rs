use std::ffi::CStr;
use std::os::raw::{c_char, c_int};
use once_cell::sync::Lazy;
use tokio::runtime::Runtime;

static RUNTIME: Lazy<Runtime> = Lazy::new(|| {
    Runtime::new().expect("Failed to initialize Tokio runtime")
});

#[no_mangle]
pub extern "C" fn check_link(url_ptr: *const c_char) -> c_int {
    if url_ptr.is_null() {
        return -1; 
    }

    let c_str = unsafe { CStr::from_ptr(url_ptr) };
    let url_str = match c_str.to_str() {
        Ok(s) => s,
        Err(_) => return -2, 
    };

    let check_result = RUNTIME.block_on(async {
        lychee_lib::check(url_str).await
    });

    match check_result {
        Ok(response) => {
            if response.status().is_success() {
                0 
            } else {
                1 
            }
        }
        Err(_) => 2, 
    }
}
