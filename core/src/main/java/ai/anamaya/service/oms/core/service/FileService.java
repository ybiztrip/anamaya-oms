package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FileFetchRequest;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final Map<String, FileProvider> fileProvider;

    private FileProvider getFileProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FileProvide";
        FileProvider provider = fileProvider.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripHotelProvider'", key);
            provider = fileProvider.get("biztripFileProvider");
        }

        return provider;
    }

    public byte[] fetch(CallerContext callerContext, FileFetchRequest request) {
        FileProvider provider = getFileProvider("biztrip");
        return provider.fetch(callerContext, request);
    }
}
