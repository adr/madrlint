package neutra1.linter.ffi;

import java.lang.foreign.SymbolLookup;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.Arena;
import java.lang.invoke.MethodHandle;
import java.lang.foreign.Linker;
import java.nio.file.Path;

public class LycheeWrapper {
    private static final MethodHandle checkLinkHandle;

    static {
        Path dllPath = Path.of("app/native/rust/target/release/lychee.dll");

        try {
            SymbolLookup lib = SymbolLookup.libraryLookup(dllPath, Arena.global());
            MemorySegment symbol = lib.find("check_link")
                    .orElseThrow(() -> new UnsatisfiedLinkError("Symbol check_link not found!"));
            FunctionDescriptor descriptor = FunctionDescriptor.of(
                    ValueLayout.JAVA_INT, 
                    ValueLayout.ADDRESS
            );
            
            checkLinkHandle = Linker.nativeLinker().downcallHandle(symbol, descriptor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }

    public static int checkLink(String url) {
        if (checkLinkHandle == null) {
            throw new IllegalStateException("Native library is not loaded.");
        }
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment nativeString = arena.allocateFrom(url);
            return (int) checkLinkHandle.invokeExact(nativeString);
        } catch (Throwable t) {
            System.err.println("FFI call failed: " + t.getMessage());
            return -3;
        }
    }
}

