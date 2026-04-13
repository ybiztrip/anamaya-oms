package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FileFetchRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.service.FileProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripFileProvider")
@RequiredArgsConstructor
public class BiztripFileProvider implements FileProvider {

    private final BiztripFileFetchService fileFetchService;

    @Override
    public byte[] fetch(CallerContext callerContext, FileFetchRequest request) {
        return fileFetchService.fetch(callerContext, request);
    }

}
