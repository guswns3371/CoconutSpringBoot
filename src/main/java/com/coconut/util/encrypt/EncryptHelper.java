package com.coconut.util.encrypt;

public interface EncryptHelper {

    String encrypt(String password);

    boolean isMatch(String password, String hashed);
}
