package neutra1.linter.ffi;

import java.lang.foreign.SymbolLookup;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.Arena;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import neutra1.linter.models.records.LinkInfo;

public class LycheeWrapper {

    private static class HandleHolder {
        private static final MethodHandle checkLinksBatchHandle;
        private static final MethodHandle freeRustStringHandle;

        static {
            Path dllPath = resolveLibraryPath();
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
                throw new RuntimeException("Failed to load native library from: " + dllPath.toAbsolutePath(), e);
            }
        }
    }

    private static Path resolveLibraryPath() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String libName = os.contains("win") ? "lychee.dll" : (os.contains("mac") ? "liblychee.dylib" : "liblychee.so");

        String exePathStr = ProcessHandle.current().info().command().orElse(null);
        if (exePathStr != null 
                && !exePathStr.endsWith("java.exe") 
                && !exePathStr.endsWith("java") 
                && !exePathStr.endsWith("javaw.exe")) {
            Path exeDir = Path.of(exePathStr).getParent();
            if (exeDir != null) {
                Path deployedPath = exeDir.resolve(libName);
                if (Files.exists(deployedPath)) {
                    return deployedPath;
                }
            }
        }

        try {
            java.net.URI location = LycheeWrapper.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path classPath = Path.of(location);
            Path exeDir = Files.isDirectory(classPath) ? classPath : classPath.getParent();
            if (exeDir != null) {
                Path deployedPath = exeDir.resolve(libName);
                if (Files.exists(deployedPath)) {
                    return deployedPath;
                }
            }
        } catch (Exception ignored) {}

        Path localPath = Path.of(libName);
        if (Files.exists(localPath)) return localPath;

        Path devPath = Path.of("app/native/rust/target/release/" + libName);
        if (Files.exists(devPath)) return devPath;

        Path siblingDevPath = Path.of("rust/target/release/" + libName);
        if (Files.exists(siblingDevPath)) return siblingDevPath;

        return devPath;
    }

    public static List<String> getBadLinks(List<LinkInfo> linkInfos) {
        StringBuilder sb = new StringBuilder();
        for (LinkInfo linkInfo : linkInfos) {
            sb.append(linkInfo.url()).append("\n");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment inputSegment = arena.allocateFrom(sb.toString());
            
            MemorySegment outputSegment = (MemorySegment) HandleHolder.checkLinksBatchHandle.invokeExact(inputSegment);
            if (outputSegment.equals(MemorySegment.NULL)) {
                throw new RuntimeException("ERROR: NULL POINTER RETURNED");
            }
            MemorySegment unboundedSegment = outputSegment.reinterpret(Long.MAX_VALUE);
            String resultStr = unboundedSegment.getString(0);
            if (resultStr.isBlank()) {
                HandleHolder.freeRustStringHandle.invokeExact(outputSegment);
                return new ArrayList<>();
            }   
            HandleHolder.freeRustStringHandle.invokeExact(outputSegment);
            return Arrays.asList(resultStr.split("\n"));
        } catch (Throwable t) {
            throw new RuntimeException("Batch native call failed", t);
        }
    }
}

