package com.stroxler.entity;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stroxler.Main;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Main.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

    @LocalServerPort
    private int port;

    private HttpClient client = HttpClientBuilder.create().build();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void TestHelloNoParameter() throws Throwable {
        HttpResponse response = client.execute(new HttpGet(getUrl("/hello")));
        assertEquals(200, response.getStatusLine().getStatusCode(),
                "hello should return status 200");
        assertEquals("Hello World!", getContent(response));
    }

    @Test
    public void TestHelloWithParameter() throws Throwable {
        HttpResponse response = client.execute(new HttpGet(getUrl("/hello?name=Steven")));
        assertEquals(200, response.getStatusLine().getStatusCode(),
                "hello should return status 200");
        assertEquals("Hello Steven!", getContent(response));
    }

    @Test
    public void TestTreeEcho() throws Throwable {
        TreeNode original = new TreeNode(
                "root content",
                new TreeNode("left content"),
                new TreeNode("right content"));
        HttpPut request = treePutRequest("/tree/echo", original);
        HttpResponse response = client.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode(),
                "hello should return status 200");
        assertEquals(original, getTree(response),
                "echo tree should match original");
    }

    @Test
    public void TestTreeReverse() throws Throwable {
        TreeNode original = new TreeNode(
                "root content",
                new TreeNode("left content"),
                new TreeNode("right content"));
        HttpPut request = treePutRequest("/tree/reverse", original);
        HttpResponse response = client.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode(),
                "hello should return status 200");
        assertEquals(TreeNode.reverse(original), getTree(response),
                "echo tree should match original");
    }

    private HttpPut treePutRequest(String uri, TreeNode treeNode)
            throws UnsupportedEncodingException, JsonProcessingException {
        String body = objectMapper.writeValueAsString(treeNode);
        HttpEntity bodyEntity = new StringEntity(body);
        HttpPut request = new HttpPut(getUrl(uri));
        request.setEntity(bodyEntity);
        request.addHeader(new BasicHeader("Content-Type", "application/json"));
        return request;
    }


    private String getUrl(String uri) {
        return "http://localhost:" + port + uri;
    }

    private TreeNode getTree(HttpResponse response) throws IOException {
        String content = getContent(response);
        byte[] contentBytes = content.getBytes();
        return objectMapper.readValue(contentBytes, TreeNode.class);
    }

    private String getContent(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }
}