package org.example.functions;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

public class Youtube implements HttpFunction {
  private static String API_KEY = "";

  private static YouTube youtube;

  @Override
  public void service(HttpRequest request, HttpResponse response)
          throws IOException, GeneralSecurityException {
    youtube = new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            null
    ).setApplicationName("YouTubeVideoRanking").build();

    setApiKEY();

    Youtube controller = new Youtube();
    controller.fetchVideoRankings();
  }

  private void setApiKEY() {
    Yaml yaml = new Yaml();
    try(InputStream in = yaml.getClass().getResourceAsStream("/config.yaml")){
      if (in == null) {
        System.err.println("Could not find config.yaml on the classpath.");
        return;
      }
      Map<String, Object> config = yaml.load(in);
      API_KEY = (String) config.get("api_key");
    } catch (Exception e) {
      System.out.println("apikey");
      e.printStackTrace();
    }

  }

  public void fetchVideoRankings() throws IOException {

    List<Integer> videoCategoryIds = Arrays.asList(0, 10, 20);
    List<List<Object>> latestVideoRanking = null;
    List<List<Object>> musicVideoRanking = null;
    List<List<Object>> gameVideoRanking = null;

    for (Integer videoCategoryId : videoCategoryIds) {
      VideoListResponse videoListResponse = fetchVideosByCategory(videoCategoryId);
      List<List<Object>> parsedVideos = parseVideos(videoListResponse, videoCategoryId);

      switch (videoCategoryId) {
        case 0:
          latestVideoRanking = parsedVideos;
          break;
//        case 10:
//          musicVideoRanking = parsedVideos;
//          break;
//        case 20:
//          gameVideoRanking = parsedVideos;
//          break;
      }
    }

    System.out.println(latestVideoRanking);
  }

  private VideoListResponse fetchVideosByCategory(int videoCategoryId) throws IOException {
    YouTube.Videos.List videoRequest = youtube.videos().list(Collections.singletonList("id,snippet,statistics"));
    videoRequest.setChart("mostPopular");
    videoRequest.setMaxResults(10L);
    videoRequest.setRegionCode("JP");
    videoRequest.setVideoCategoryId(String.valueOf(videoCategoryId));
    videoRequest.setKey(API_KEY);
    return videoRequest.execute();
  }

  private List<List<Object>> parseVideos(VideoListResponse videoListResponse, int videoCategoryId) {
    List<List<Object>> parsedVideos = new ArrayList<>();
    List<Video> videos = videoListResponse.getItems();
    for (int i = 0; i < videos.size(); i++) {
      Video video = videos.get(i);
      List<Object> videoData = Arrays.asList(
              i + 1,
              "https://www.youtube.com/watch?v=" + video.getId(),
              video.getStatistics().getViewCount().toString()
      );
      parsedVideos.add(videoData);
    }
    return parsedVideos;
  }

}
