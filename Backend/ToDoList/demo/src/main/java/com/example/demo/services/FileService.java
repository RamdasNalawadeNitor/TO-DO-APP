package com.example.demo.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import com.example.demo.entities.Task;
import com.example.demo.repositories.TaskRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

@Service
public class FileService {

    @Autowired
    private TaskRepository taskRepository;

    @Value("${task.file}")
    private String uploadDir; // Ensure this is set in your properties file

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String filename = multipartFile.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        Path directoryPath = Paths.get(uploadDir);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath); // Create directory if it doesn't exist
        }

        Path filePath = directoryPath.resolve(filename);
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // Replace if exists
        return filePath.toString(); // Return the file path for saving in the database
    }


    public Resource loadFileAsResource(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        String filePath = task.getFilePath().replace("\\", "/");
        Path path = Paths.get(filePath);

        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    public String previewFile(Long taskId) throws IOException {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        String filePath = task.getFilePath().replace("\\", "/");
        Path path = Paths.get(filePath);

        // Read the PDF file and extract text
        try (PDDocument document = PDDocument.load(path.toFile())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            throw new IOException("Failed to extract text from PDF", e);
        }
    }
}
