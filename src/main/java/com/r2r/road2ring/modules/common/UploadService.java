package com.r2r.road2ring.modules.common;

import static com.r2r.road2ring.modules.common.Static.IMAGE_ASSETS;
import static com.r2r.road2ring.modules.common.Static.SIZE_100KB;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

  R2rTools r2rTools;

  @Value("${r2r.upload.path}")
  String imagePath;

  @Value("${r2r.spaces.full-link}")
  String fullLink;

  @Autowired
  AmazonS3 s3Client;

  @Autowired
  Environment env;

  @Value("${r2r.bucket.name}")
  String S3_BUCKET_NAME;


  @Value("${r2r.picfolder.name}")
  String FOLDER;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  public void setR2rTools(R2rTools r2rTools) {
    this.r2rTools = r2rTools;
  }
  public String uploadImagePicture(MultipartFile file, String typeImage)
      throws IOException, FileSizeLimitExceededException {

    String linkUrl = "";
    String type = file.getContentType().split("/")[1];
    Long date = new Date().getTime();

    if (file.getSize() > SIZE_100KB) {
      throw new FileSizeLimitExceededException("File is too Big", file.getSize(), SIZE_100KB);
    }
    try {
      String picName = date + r2rTools.generateRandomCode(8);
      linkUrl = fullLink + FOLDER + picName + "." + type;
      File convFile = convertMultiPartToFile(file);
      this.uploadToSpaces(convFile, picName, type);
      convFile.delete();
    } catch (Exception e){
      e.printStackTrace();
    }


    return linkUrl;

  }git

  public void uploadToSpaces(File file, String imgName, String ext) throws IOException {
    String key = FOLDER + imgName + "." + ext;
    s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, key, file)
        .withCannedAcl(CannedAccessControlList.PublicRead));
  }

  public void deleteFile(String fileName) throws Exception {
    s3Client.deleteObject(new DeleteObjectRequest(S3_BUCKET_NAME, FOLDER + fileName));
  }

  private File convertMultiPartToFile(MultipartFile file) throws IOException {
    File convFile = new File(file.getOriginalFilename());
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(file.getBytes());
    fos.close();
    return convFile;
  }

}
