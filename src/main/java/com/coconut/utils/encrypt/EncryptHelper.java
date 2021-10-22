package com.coconut.utils.encrypt;

public interface EncryptHelper {

    String encrypt(String password);

    boolean isMatch(String password, String hashed);
}
