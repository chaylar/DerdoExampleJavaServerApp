package org.derdoapp.Helper;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.derdoapp.DataManager.TokenGenerator;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {

    private final String blobConnectionStr = "DefaultEndpointsProtocol=https;AccountName=derdstorage;AccountKey=eTbA1RI6dBsCegylwGb02jGjtwYPsELYKPGz0o3YgOFIilJyD2wfa49Sy9FTOIwa+gF/BS6yvtu+tPR5Dxor8A==;EndpointSuffix=core.windows.net";
    private final String blobContainerName = "derdcontainer";

    public String uploadFile(MultipartFile file) throws Exception {
        return uploadToBlob(file);

        /*String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String saveFileName = TokenGenerator.GenerateToken().token + "." + fileExtension;
        //Path copyLocation = Paths.get(fileStorageConfig.getUploadDir() + File.separator + StringUtils.cleanPath(saveFileName));
        Path copyLocation = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(saveFileName));
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        return saveFileName;*/
    }

    private BlobContainerClient getBlobClient() {
        // Create a BlobServiceClient object which will be used to create a container client
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(blobConnectionStr).buildClient();
        return blobServiceClient.getBlobContainerClient(blobContainerName);
    }

    private String uploadToBlob(MultipartFile file) throws Exception {

        BlobContainerClient containerClient = getBlobClient();

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String saveFileName = TokenGenerator.GenerateToken().token + "." + fileExtension;

        BlobClient blobClient = containerClient.getBlobClient(saveFileName);
        blobClient.upload(file.getInputStream(), file.getSize());

        return blobClient.getBlobUrl();

        //return saveFileName;
    }

    public String uploadResizedImageToBlob(BufferedImage bfImage, String fileExtension) {
        try {
            BlobContainerClient containerClient = getBlobClient();
            String saveFileName = TokenGenerator.GenerateToken().token + "." + fileExtension;

            byte[] imageFile = toByteArray(bfImage, fileExtension);

            BlobClient blobClient = containerClient.getBlobClient(saveFileName);
            InputStream targetStream = new ByteArrayInputStream(imageFile);
            blobClient.upload(targetStream, imageFile.length);

            return blobClient.getBlobUrl();
        }
        catch (Exception ex) {
            System.out.println("uploadToBlobResizedImage.EX : " + ex.getMessage() != null ? ex.getMessage() : "NULL_MESSAGE");
        }

        return null;
    }

    private byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }

    //TODO : THIS IS UNNECESSARY!!!!
    /*public String getFilePathByNameFromLocal(String fileName) throws Exception {
        if(fileName == null || fileName == "") {
            return null;
        }

        BlobContainerClient containerClient = getBlobClient();
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        return URLEncoder.encode(blobClient.getBlobUrl(), StandardCharsets.UTF_8.toString());
    }*/
}
