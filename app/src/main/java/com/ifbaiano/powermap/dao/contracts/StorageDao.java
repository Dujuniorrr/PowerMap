package com.ifbaiano.powermap.dao.contracts;

public interface StorageDao {

    String add(byte[] imageData, String child);
    String putImage(byte[] imageData, String child);
    Boolean remove(String child);

}
