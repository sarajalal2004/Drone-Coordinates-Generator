package com.ga.DroneCoordinatesGenerator.controller;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import com.ga.DroneCoordinatesGenerator.service.DroneImageProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("api")
public class DroneImageProcessorController {
    private DroneImageProcessorService droneImageProcessorService;

    @Autowired
    public void setDroneImageProcessorService(DroneImageProcessorService droneImageProcessorService){
        this.droneImageProcessorService = droneImageProcessorService;
    }

    @PostMapping(path = "/process")
    public ResponseEntity processImage(@RequestParam("file") MultipartFile file,
                                       @RequestParam("numberOfDrones") Integer numberOfDrones,
                                       @RequestParam("width") Double width) throws IOException {
        return droneImageProcessorService.processImage(file, numberOfDrones, width);
    }
}
