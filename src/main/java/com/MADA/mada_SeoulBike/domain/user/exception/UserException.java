package com.MADA.mada_SeoulBike.domain.user.exception;

import com.MADA.mada_SeoulBike.global.exception.MyErrorCode;
import com.MADA.mada_SeoulBike.global.exception.MyException;

public class UserException extends MyException {
    public UserException(MyErrorCode errorCode) {
        super(errorCode);
    }
}
