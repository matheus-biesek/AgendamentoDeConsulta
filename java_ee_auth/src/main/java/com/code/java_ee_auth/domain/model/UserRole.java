package com.code.java_ee_auth.domain.model;

import java.util.UUID;

import com.code.java_ee_auth.domain.enuns.Roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    public UUID user_id;
    public Roles role_name;
}
