package com.ga.DroneCoordinatesGenerator.service;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import com.ga.DroneCoordinatesGenerator.model.*;
import com.ga.DroneCoordinatesGenerator.repository.HistoryRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ga.DroneCoordinatesGenerator.service.UserService.getCurrentLoggedInUser;

@Service
public class DroneImageProcessorService {
    private HistoryRepository historyRepository;
    private CloudinaryService cloudinaryService;

    @Autowired
    public  DroneImageProcessorService(HistoryRepository historyRepository,
                                       CloudinaryService cloudinaryService){
        this.historyRepository = historyRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public ResponseEntity processImage(MultipartFile file, Integer numberOfDrones, Double width) throws IOException {
        if(getCurrentLoggedInUser().getOwnedDrone().size() + getCurrentLoggedInUser().getRentedDrone().size() < numberOfDrones)
            throw new BadRequestException("You don't have enough number of drones");
        BufferedImage image = ImageIO.read(file.getInputStream());
        GrayU8 grayImage = ConvertBufferedImage.convertFrom(image, (GrayU8) null);

        CannyEdge<GrayU8, GrayS16> canny = FactoryEdgeDetectors.canny(2, true, true, GrayU8.class, GrayS16.class);
        GrayU8 edgeImage = new GrayU8(grayImage.width, grayImage.height);
        canny.process(grayImage, 0.1f, 0.3f, edgeImage);

        @Setter@Getter
        @AllArgsConstructor
        class Points{
            private int x;
            private int y;
            private boolean visited;
        }

        List<Points> edgePixels = new ArrayList<>();
        Map<String, Points> edgeMap = new HashMap<>();

        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

        // push all edges points
        for (int y = 0; y < edgeImage.height; y++) {
            for (int x = 0; x < edgeImage.width; x++) {
                if (edgeImage.get(x, y) > 0) {
                    Points p = new Points(x, y, false);
                    edgePixels.add(p);
                    edgeMap.put(x + "," + y, p);
                }
            }
        }

        // find the start of edges
        List<Points> startOfEdges = new ArrayList<>();
        int count;
        for (Points p : edgePixels) {
            count = 0;
            for (int i = 0; i < 8; i++) {
                int nx = p.getX() + dx[i];
                int ny = p.getY() + dy[i];
                if (nx >= 0 && nx < edgeImage.width &&
                        ny >= 0 && ny < edgeImage.height &&
                        edgeMap.containsKey(nx + "," + ny)) {
                    count++;
                }
            }
            // end of a line = 1 neighbor, so it is one of lines starts
            if (count == 1) startOfEdges.add(p);
        }

        int distance = edgePixels.size() / numberOfDrones;
        List<Points> dronesPoints = new ArrayList<>();

        int globalStepCounter = 0;
        for(Points point: startOfEdges){
            if (point.isVisited()) continue;
            Points current = point;

            while (dronesPoints.size() < numberOfDrones){
                current.setVisited(true);

                if(globalStepCounter % distance == 0)
                    dronesPoints.add(current);

                globalStepCounter++;

                Points next = null;
                for (int i=0; i<8; i++){
                    int nx = current.getX() + dx[i];
                    int ny = current.getY() + dy[i];

                    Points neighbor = edgeMap.get(nx + "," + ny);
                    if(neighbor != null && !neighbor.isVisited()){
                        next = neighbor;
                        break;
                    }
                }
                if(next == null)
                    break;
                current = next;
            }
        }

        List<Points> notVisited = edgePixels.stream().filter(p -> !p.isVisited()).toList();

        for(Points point: notVisited){
            if (point.isVisited()) continue;
            Points current = point;
            while (dronesPoints.size() < numberOfDrones){
                current.setVisited(true);
                if(globalStepCounter % distance == 0)
                    dronesPoints.add(current);
                globalStepCounter++;
                Points next = null;
                for (int i=0; i<8; i++){
                    int nx = current.getX() + dx[i];
                    int ny = current.getY() + dy[i];

                    Points neighbor = edgeMap.get(nx + "," + ny);
                    if(neighbor != null && !neighbor.isVisited()){
                        next = neighbor;
                        break;
                    }
                }
                if(next == null)
                    break;
                current = next;
            }
        }

        notVisited = edgePixels.stream().filter(p -> !p.isVisited()).toList();

        if(dronesPoints.size() < numberOfDrones && !notVisited.isEmpty()){
            distance = Math.max(1,notVisited.size() / (numberOfDrones - dronesPoints.size()));
            for (Points point: notVisited){
                point.setVisited(true);
                if(globalStepCounter % distance == 0 && dronesPoints.size() < numberOfDrones)
                    dronesPoints.add(point);
                globalStepCounter ++;
            }
        }

        System.out.println("edgePixels:   " + edgePixels.size());
        System.out.println("startOfEdges: " + startOfEdges.size());
        System.out.println("distance:     " + distance);
        System.out.println("dronesPoints: " + dronesPoints.size());

        BufferedImage resultBuffered = new BufferedImage(grayImage.width, grayImage.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resultBuffered.createGraphics();



        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, grayImage.width, grayImage.height);


//        To view outline if needed
//        for(Points p: edgePixels){
//            resultBuffered.setRGB(p.x, p.y, Color.lightGray.getRGB());
//        }

        for (Points p: dronesPoints){
            g2d.setColor(Color.PINK);
            g2d.fillOval(p.getX() - 2, p.getY() -2, 4, 4);
            g2d.setColor(Color.BLACK);
            //g2d.setFont(new Font("Arial", Font.BOLD, 10));
           // g2d.drawString("1", p.getX() + 5, p.getY() - 5);
        }

        g2d.dispose();
        History history = new History();
        history.setUser(getCurrentLoggedInUser());
        history.setNumberOfDrones(dronesPoints.size());
        history.setWidth(width);
        int owned = getCurrentLoggedInUser().getOwnedDrone().size();
        Double metersPerPixel = width / resultBuffered.getWidth();

        History historyObj = historyRepository.save(history);

        for(int i=0; i< dronesPoints.size(); i++){
            DroneHistory droneHistory = new DroneHistory();
            droneHistory.setHistory(historyObj);
            droneHistory.setDrone(
                    i < owned
                            ? getCurrentLoggedInUser().getOwnedDrone().get(i)
                            : getCurrentLoggedInUser().getRentedDrone().get(i - owned)
            );
            droneHistory.setCoordinate(new Coordinate(
                    dronesPoints.get(i).getX() * metersPerPixel, dronesPoints.get(i).getY() * metersPerPixel
            ));
            history.getDroneHistories().add(droneHistory);
        }
        historyObj = historyRepository.save(history);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(resultBuffered, "png", output);
        byte[] resultBytes = output.toByteArray();

        MultipartFile resultFile = new MockMultipartFile(
                "file",
                "history_" + historyObj.getId() + ".png",
                "image/png",
                resultBytes
        );

        String filename = cloudinaryService.uploadProfileImage(resultFile, "history" + historyObj.getId());
        historyObj.setImage(filename);
        historyRepository.save(historyObj);

        HistoryResponseDTO response = new HistoryResponseDTO();
        response.setId(historyObj.getId());
        response.setImage(historyObj.getImage());
        response.setNumberOfDrones(historyObj.getNumberOfDrones());
        response.setWidth(historyObj.getWidth());
        response.setDroneHistories(
                historyObj.getDroneHistories().stream().map(dh -> {
                    HistoryResponseDTO.DroneHistoryDTO dto = new HistoryResponseDTO.DroneHistoryDTO();
                    dto.setDroneId(dh.getDrone().getId());
                    dto.setDroneSerial(dh.getDrone().getSerial());
                    dto.setX(dh.getCoordinate().getX());
                    dto.setY(dh.getCoordinate().getX());
                    return dto;
                }).toList()
        );

        return ResponseEntity.ok(response);
    }
}
