package org.derdoapp.DerdoAppServer;

import org.derdoapp.Helper.EncryptionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncryptTester {

    @Test
    public void encryptTest() throws Exception {

        String testMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ut ante molestie, ultricies orci eget, gravida sapien. Integer id augue commodo, pharetra sem vitae, vehicula erat. Aenean id justo eget magna consequat pretium non nec risus. Pellentesque vel condimentum est. Quisque nec faucibus elit. Nulla vitae felis lacinia, rutrum libero ut, suscipit lorem. Nulla facilisi. Suspendisse consectetur arcu ut ex elementum blandit. Ut consequat lorem sed interdum lacinia. Quisque ultrices gravida lectus, id tempor lorem pretium nec. Curabitur nec tortor posuere, fringilla sapien ut, interdum ex. Aenean semper, enim id congue finibus, diam leo suscipit dui, et accumsan odio lacus vitae lectus.";

        //String enc = EncryptionHelper.encryptMessage(testMessage);
        //String dec = EncryptionHelper.decryptMessage(enc);

        String enc = EncryptionHelper.encrypt(testMessage);
        String dec = EncryptionHelper.decrypt(enc);

        System.out.println("ecn : " + enc);
        System.out.println("dec : " + dec);

        Assertions.assertEquals(testMessage, dec);
    }

}
