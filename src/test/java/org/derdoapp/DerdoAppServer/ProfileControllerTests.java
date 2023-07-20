package org.derdoapp.DerdoAppServer;

import org.apache.commons.io.IOUtils;
import org.derdoapp.Controller.ProfileController;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.Helper.FileHelper;
import org.derdoapp.Helper.ImageResizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
public class ProfileControllerTests extends BaseControllerTest {

    @Autowired
    private ProfileController profileController;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void uploadProfileImageTest() throws Exception {

        //removeTestUsersOnDB();
        //saveTestUser();

        //Required
        File file = new File("/Users/Cag/Documents/Projects/gereks/kediler.jpg");
        Assert.notNull(file);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        Assert.notNull(appUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
        file.getName(), "text/plain", IOUtils.toByteArray(input));
        //

        profileController.uploadProfileImage(multipartFile);
    }

    @Test
    public void resizeImageTest() throws Exception {

        //Required
        File file = new File("/Users/Cag/Documents/Projects/gereks/kediler.jpg");
        Assert.notNull(file);

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        //

        //profileController.uploadProfileImage(multipartFile);
        ImageResizer resizer = new ImageResizer();
        BufferedImage bufferedImage = resizer.resizeFile(multipartFile);

        FileHelper fileHelper = new FileHelper();
        String format = "jpg";
        String savedFileName = fileHelper.uploadResizedImageToBlob(bufferedImage, format);

        Assertions.assertNotNull(savedFileName);

        System.out.println(savedFileName);
    }

}
