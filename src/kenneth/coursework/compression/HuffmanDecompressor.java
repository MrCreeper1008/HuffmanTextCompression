package kenneth.coursework.compression;

import kenneth.coursework.exceptions.IncorrectFormatException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HuffmanDecompressor {
    public void decompress(String inputFile, String dest) throws IOException, IncorrectFormatException {
        final var fileInput = new DataInputStream(new FileInputStream(inputFile));

        final var treeSize = fileInput.readInt();
        final var serializedTree = new byte[treeSize];

        for (int i = 0; i < treeSize; i++) {
            serializedTree[i] = fileInput.readByte();
        }

        final var huffmanTree = HuffmanTree.deserialize(new String(serializedTree, StandardCharsets.UTF_8));
        final var fileOutput = new DataOutputStream(new FileOutputStream(dest));
        var fileSize = fileInput.readLong();

        var bytesWritten = 0L;
        var root = huffmanTree.getRoot();
        var node = root;

        for (var b = fileInput.read(); b >= 0; b = fileInput.read()) {
            // read from the start of a byte
            var mask = 0b10000000;
            var pos = 7;
            while (mask > 0) {
                var bit = (mask & b) >>> pos--;

                final var nextNode = (HuffmanTree.HuffmanNode) (bit == 1 ? node.getRightNode() : node.getLeftNode());
                final var nextByte = nextNode.getByte();

                if (nextByte != null) {
                    System.out.println(nextByte);
                    fileOutput.write(nextByte);
                    if (++bytesWritten == fileSize) break;
                    node = root;
                } else {
                    node = nextNode;
                }

                mask >>>= 1;
            }
        }

        fileInput.close();
        fileOutput.close();
    }
}