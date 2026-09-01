package neutra1.linter.ffi;

import java.lang.foreign.SymbolLookup;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.Arena;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import neutra1.linter.models.records.LinkInfo;

public class LycheeWrapper {
    private static final MethodHandle checkLinksBatchHandle;
    private static final MethodHandle freeRustStringHandle;

    static {
        Path dllPath = Path.of("app/native/rust/target/release/lychee.dll");

        try {
            SymbolLookup lib = SymbolLookup.libraryLookup(dllPath, Arena.global());
            Linker linker = Linker.nativeLinker();

            MemorySegment batchSymbol = lib.find("check_links_batch").orElseThrow();
            checkLinksBatchHandle = linker.downcallHandle(
                    batchSymbol, 
                    FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS)
            );

            MemorySegment freeSymbol = lib.find("free_rust_string").orElseThrow();
            freeRustStringHandle = linker.downcallHandle(
                    freeSymbol, 
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }

    public static List<String> getBadLinks(List<LinkInfo> linkInfos) {
        StringBuilder sb = new StringBuilder();
        for (LinkInfo linkInfo : linkInfos) {
            sb.append(linkInfo.url()).append("\n");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment inputSegment = arena.allocateFrom(sb.toString());
            MemorySegment outputSegment = (MemorySegment) checkLinksBatchHandle.invokeExact(inputSegment);
            if (outputSegment.equals(MemorySegment.NULL)) {
                throw new RuntimeException("ERROR: NULL POINTER RETURNED");
            }
            MemorySegment unboundedSegment = outputSegment.reinterpret(Long.MAX_VALUE);
            String resultStr = unboundedSegment.getString(0);
            if (resultStr.isBlank()) {
                freeRustStringHandle.invokeExact(outputSegment);
                return new ArrayList<>();
            }   
            freeRustStringHandle.invokeExact(outputSegment);
            return Arrays.asList(resultStr.split("\n"));

            
        } catch (Throwable t) {
            throw new RuntimeException("Batch native call failed", t);
        }
    }
}

