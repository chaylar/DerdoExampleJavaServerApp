package org.derdoapp.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.SocketIOManagerChatRoom.SocketManagerChatRoom.ChatRoomSocketIOServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value ="/socket.io")
public class ChatController extends BaseController {

    //@Autowired
    //private ChatRoomSocketIOServer chatRoomSocketIOServer;

    //private final EngineIoServer mEngineIoServer = new EngineIoServer();

    @ApiOperation(value = "Connects user to chat client as listener", response = Boolean.class)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void connect(
            @ApiParam(value = "Token parameter")
            @RequestParam(value = "token") String token,
            @ApiParam(value = "UserId to chat")
            @RequestParam(value = "userId")
            String userId) throws Exception {

        System.out.println("incoming.token : " + token);
        System.out.println("incoming.userId : " + userId);

        if(token == null || token.equals("")) {
            System.out.println("incoming.token : " + token);
            return;
        }

        AppUser appUser = appUserRepository.findByAccessToken(token);
        if (appUser == null) {
            System.out.println("incoming.refused.token : " + token);
            return;
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            System.out.println("ending");
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();

        //Boolean result = chatRoomSocketIOServer.onServletRequest(request, response, token, userId);
        //chatRoomSocketIOServer.onServletRequest(request, response, appUser.id, userId);
        //chatRoomSocketIOServer.onServletRequest(request, response, "1", "1");
        ChatRoomSocketIOServer.getInstance().onServletRequest(request, response, appUser.id, userId);
    }
}
