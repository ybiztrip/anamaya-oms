package ai.anamaya.service.oms.core.client.chatEngine.mapper;

import ai.anamaya.service.oms.core.client.chatEngine.dto.request.ChatEngineUserRegisterRequest;
import ai.anamaya.service.oms.core.entity.User;

public class ChatEngineUserRegisterMapper {
    public ChatEngineUserRegisterRequest map(User user) {
        ChatEngineUserRegisterRequest req = new ChatEngineUserRegisterRequest();
        req.setCountryCode(user.getCountryCode());
        req.setPhoneNumber(user.getPhoneNo());
        req.setEmail(user.getEmail());
        req.setFirstName(user.getFirstName());
        req.setLastName(user.getLastName());

        ChatEngineUserRegisterRequest.UserData userData =
            new ChatEngineUserRegisterRequest.UserData();
        userData.setGender(user.getGender());
        req.setUserData(userData);

        return req;
    }
}
