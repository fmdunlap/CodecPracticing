package com.fdunlap.codecs;

import java.util.PriorityQueue;

/**
 * Created by FDUNLAP on 2/1/2017.
 */
class Node implements Comparable<Node> {
    Node left;
    Node right;
    Node parent;
    int val;
    int frequency;
    int bin;

    public Node(int valIn, int frequencyIn) {
        val = valIn;
        frequency = frequencyIn;
    }

    public Node(int frequencyIn) {
        val = -1;
        frequency = frequencyIn;
    }

    public int compareTo(Node n) {
        if (frequency < n.frequency) {
            return -1;
        }
        else if(frequency > n.frequency) {
            return 1;
        }
        return 0;
    }

    // Returns root node to pass to printFromPreOrder
    public static Node makeHuffmanTree(int frequencies[], int keys[]) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (int i = 0; i < keys.length; i++) {
            Node n = new Node(keys[i], frequencies[i]);
            queue.add(n);
        }
        Node root = null;
        while (queue.size() > 1)  {
            Node least1 = queue.poll();
            least1.bin = 1;
            Node least2 = queue.poll();
            least2.bin = 0;
            Node combined = new Node(least1.frequency + least2.frequency);
            combined.right = least1;
            combined.left = least2;
            least1.parent = combined;
            least2.parent = combined;
            queue.add(combined);
            // Keep track until we actually find the root
            root = combined;
        }
        return root;
    }
}
