package com.MADA.mada_SeoulBike.user.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String email;
    private String currentPassword;  // 현재 비밀번호
    private String newPassword;      // 새 비밀번호

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", currentPassword='" + (currentPassword != null ? "***" : "null") + '\'' +
                ", newPassword='" + (newPassword != null ? "***" : "null") + '\'' +
                '}';
    }
}
