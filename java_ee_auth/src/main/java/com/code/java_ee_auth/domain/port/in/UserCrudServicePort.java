package com.code.java_ee_auth.domain.port.in;

import com.code.java_ee_auth.domain.dto.request.ChangeDataUserDTO;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.response.MessageDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;

public interface UserCrudServicePort {
    UserDataDTO getUserData(String cpf);
    MessageDTO updateUserSecretary(ChangeDataUserDTO changeDataUserDTO);
    MessageDTO updateUserAdmin(UpdateRoleDTO updateRoleDTO);
    MessageDTO deleteUser(String cpf);
}
