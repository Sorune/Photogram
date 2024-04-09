package com.sorune.photogram.Upload.Controller;

import com.sorune.photogram.Upload.Domain.UploadResultVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@Log4j2
public class UploadContorller {
    //설정한 파일 경로 가져오기
    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<List<UploadResultVo>> upload(MultipartFile[] uploadFiles) {
        log.info("upload start...");
        List<UploadResultVo> resultList = new ArrayList<>();
        for (MultipartFile file : uploadFiles) {
            if(!Objects.requireNonNull(file.getContentType()).startsWith("image")){
                log.warn("this file is not image");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            //파일 이름 추출
            String originalFilename = file.getOriginalFilename();
            String fileName = originalFilename.substring(originalFilename.lastIndexOf("\\")+1);
            log.info("fileName : " + fileName);
            //저장 폴더 경로 생성
            String folderPath = makeFolder();
            String uuid = UUID.randomUUID().toString();
            String saveName = uploadPath+ File.separator+folderPath+File.separator+uuid+"_"+fileName;
            log.info("save name : " + saveName);
            Path savePath = Paths.get(saveName);
            try {
                file.transferTo(savePath);
                resultList.add(new UploadResultVo(fileName,uuid,folderPath,file.getContentType()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //파일 업로드 체크용 리소스
           /* ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(saveName);
            log.info(resource.getFilename());*/
        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);
        log.info("folderPath : " + folderPath);
        File uploadFolder = new File(uploadPath,folderPath);
        if(!uploadFolder.exists()){
            uploadFolder.mkdirs();
        }
        return folderPath;
    }
}
