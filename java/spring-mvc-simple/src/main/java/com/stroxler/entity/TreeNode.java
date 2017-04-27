package com.stroxler.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;


public class TreeNode {

    private String data;
    private TreeNode leftSide = null;
    private TreeNode rightSide = null;

    public TreeNode(
            @JsonProperty("content") String data) {
        this.data = data;
    }

    @JsonCreator
    public TreeNode(
            @JsonProperty("content") String data,
            @JsonProperty("left_side") TreeNode leftSide,
            @JsonProperty("right_side") TreeNode rightSide) {
        this.data = data;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    @JsonGetter("data")
    public String getData() {
        return data;
    }

    @JsonGetter("left_side")
    public TreeNode getLeftSide() {
        return leftSide;
    }

    @JsonGetter("right_side")
    public TreeNode getRightSide() {
        return rightSide;
    }

    public static TreeNode reverse(TreeNode root) {
        return new TreeNode(
                root.data,
                root.rightSide == null ? null: reverse(root.rightSide),
                root.leftSide == null ? null: reverse(root.leftSide));
    }

    @Override
    public String toString() {
        return "\nTreeNode{" +
                "\n  data='" + data + '\'' +
                ",\n  leftSide=" + leftSide +
                ",\n  rightSide=" + rightSide +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeNode treeNode = (TreeNode) o;

        if (data != null ? !data.equals(treeNode.data) : treeNode.data != null) return false;
        if (leftSide != null ? !leftSide.equals(treeNode.leftSide) : treeNode.leftSide != null) return false;
        return rightSide != null ? rightSide.equals(treeNode.rightSide) : treeNode.rightSide == null;

    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (leftSide != null ? leftSide.hashCode() : 0);
        result = 31 * result + (rightSide != null ? rightSide.hashCode() : 0);
        return result;
    }
}
