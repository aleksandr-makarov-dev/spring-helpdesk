package com.aleksandrmakarovdev.helpdesk.user.service;

import com.aleksandrmakarovdev.helpdesk.security.WebUserDetails;
import com.aleksandrmakarovdev.helpdesk.user.entity.RefreshToken;
import com.aleksandrmakarovdev.helpdesk.user.model.Token;

public interface TokenService {

    Token createRefreshToken(WebUserDetails webUserDetails);

    Token createAccessToken(WebUserDetails webUserDetails);
}
