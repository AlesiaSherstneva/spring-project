import org.springframework.web.client.RestTemplate;

public class Consumer {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://reqres.in/api/users/2";
        String response = restTemplate.getForObject(url, String.class);

        System.out.println(response);
    }
}
