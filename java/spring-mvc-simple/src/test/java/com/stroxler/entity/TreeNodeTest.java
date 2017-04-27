package com.stroxler.entity;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


public class TreeNodeTest {

    @Test
    public void testReverse() {
        TreeNode original = new TreeNode(
                "top_level",
                new TreeNode(
                        "first_level_left",
                        new TreeNode("second_level_left"),
                        new TreeNode("second_level_right")),
                new TreeNode("first_level_right")
        );
        TreeNode expected = new TreeNode(
                "top_level",
                new TreeNode("first_level_right"),
                new TreeNode( "first_level_left",
                        new TreeNode("second_level_right"),
                        new TreeNode("second_level_left"))
        );
        TreeNode actual = TreeNode.reverse(original);
        assertEquals(expected, actual,
                "TreeNode.reverse works as expected");
    }

    @Test
    public void testSerialize() throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        TreeNode original = new TreeNode(
                "top_level",
                new TreeNode(
                        "first_level_left",
                        new TreeNode("second_level_left"),
                        new TreeNode("second_level_right")),
                new TreeNode("first_level_right")
        );
        String serialized = objectMapper.writeValueAsString(original);
        // sanity check the serialization
        assertTrue(serialized.contains("left_side"));
        assertTrue(serialized.contains("right_side"));
        assertTrue(serialized.contains(original.getData()));
        assertTrue(serialized.contains(original.getLeftSide().getData()));
        assertTrue(serialized.contains(original.getRightSide().getData()));
        // make sure that deserialization reverses serialization
        TreeNode deserialized = objectMapper.readValue(serialized.getBytes(), TreeNode.class);
        assertEquals(original, deserialized);
    }

}