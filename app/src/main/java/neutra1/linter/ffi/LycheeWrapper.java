package neutra1.linter.ffi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public class LycheeWrapper {
    private static final MethodHandle printHelloHandle;

    static {
        Path dllPath = Path.of("app/native/rust/target/release/lychee.dll");

        try {
            SymbolLookup lib = SymbolLookup.libraryLookup(dllPath, Arena.global());
            MemorySegment symbol = lib.find("hello")
                    .orElseThrow(() -> new UnsatisfiedLinkError("Symbol hello not found!"));
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid();
            printHelloHandle = Linker.nativeLinker().downcallHandle(symbol, descriptor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }

    public static void hello() {
        try {
            printHelloHandle.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Native call failed", t);
        }
    }
}

