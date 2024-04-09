package com.sorune.photogram.Upload.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UploadResultVo implements Serializable {
    private String fileName;
    private String uuid;
    private String folderPath;
    private String fileType;

    public String getImgaeURL(){
        try {
            return URLEncoder.encode(folderPath+ File.separator+uuid+"_"+fileName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
