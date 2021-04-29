package com.coconut;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@SpringBootTest
class CoconutApplicationTests {

    @Test
    void contextLoads() {
        String oldReadMembersString = "[15, 3, 4]";

        ArrayList<String> oldReadMembers = new GsonBuilder().create().fromJson(oldReadMembersString, new TypeToken<ArrayList<String>>(){}.getType());
        oldReadMembers.add("3");
        oldReadMembers = new ArrayList<String>(new HashSet<String>(oldReadMembers));
        Collections.sort(oldReadMembers);
        System.out.println(oldReadMembers.toString());
    }

}
