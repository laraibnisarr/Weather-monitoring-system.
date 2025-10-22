import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherApp {
    private JFrame frame;
    private JTextField cityInput;
    private JButton getWeatherButton, viewDataButton;
    private JPanel detailsPanel;
    private JLabel weatherLabel, temperatureLabel, humidityLabel, windSpeedLabel;
    private Color defaultColor;

    public WeatherApp() {
        // Load and resize icons
        ImageIcon weatherIcon = new ImageIcon(new ImageIcon("src/images/weather_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon temperatureIcon = new ImageIcon(new ImageIcon("src/images/temperature_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon humidityIcon = new ImageIcon(new ImageIcon("src/images/humidity_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon windSpeedIcon = new ImageIcon(new ImageIcon("src/images/wind_speed_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        // Main frame setup
        frame = new JFrame("Weather Monitoring App 🌙");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 450);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30)); // Dark background

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.setBackground(new Color(30, 30, 30));

        JLabel cityLabel = new JLabel("Enter City:");
        cityLabel.setForeground(Color.WHITE);

        cityInput = new JTextField(15);
        cityInput.setBackground(new Color(45, 45, 45));
        cityInput.setForeground(Color.WHITE);
        cityInput.setCaretColor(Color.WHITE);
        cityInput.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

        getWeatherButton = new JButton("Get Weather");
        styleButton(getWeatherButton);

        viewDataButton = new JButton("Clear Data");
        styleButton(viewDataButton);

        headerPanel.add(cityLabel);
        headerPanel.add(cityInput);
        headerPanel.add(getWeatherButton);
        headerPanel.add(viewDataButton);

        // Weather details panel
        detailsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        detailsPanel.setBackground(new Color(40, 40, 40));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), "Weather Info", 0, 0, null, Color.LIGHT_GRAY));

        weatherLabel = new JLabel("Weather: ");
        temperatureLabel = new JLabel("Temperature: ");
        humidityLabel = new JLabel("Humidity: ");
        windSpeedLabel = new JLabel("Wind Speed: ");

        for (JLabel label : new JLabel[]{weatherLabel, temperatureLabel, humidityLabel, windSpeedLabel}) {
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            detailsPanel.add(label);
        }

        defaultColor = detailsPanel.getBackground();

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(detailsPanel, BorderLayout.CENTER);

        getWeatherButton.addActionListener(e -> fetchWeatherData());
        viewDataButton.addActionListener(e -> clearWeatherData());

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180)); // Steel blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
    private void fetchWeatherData() {
        String city = cityInput.getText().trim();
        if (city.isEmpty()) {
            weatherLabel.setText("Please enter a city name.");
            weatherLabel.setForeground(Color.PINK);
            return;
        }

        String apiKey = "4e70aaad02f9af43019d5dcb9e1f73bb";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        getWeatherButton.setEnabled(false);
        weatherLabel.setText("Loading...");
        weatherLabel.setForeground(Color.LIGHT_GRAY);
        temperatureLabel.setText("");
        humidityLabel.setText("");
        windSpeedLabel.setText("");

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                JSONObject jsonResponse = new JSONObject(content.toString());
                String weather = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
                double humidity = jsonResponse.getJSONObject("main").getDouble("humidity");
                double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");

                SwingUtilities.invokeLater(() -> {
                    weatherLabel.setText("Weather: " + weather);
                    weatherLabel.setForeground(Color.WHITE);
                    temperatureLabel.setText("Temperature: " + String.format("%.1f°C", temperature));
                    humidityLabel.setText("Humidity: " + String.format("%.1f%%", humidity));
                    windSpeedLabel.setText("Wind Speed: " + String.format("%.1fm/s", windSpeed));
                    getWeatherButton.setEnabled(true);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    weatherLabel.setText("Error fetching data. Try another city.");
                    weatherLabel.setForeground(Color.PINK);
                    getWeatherButton.setEnabled(true);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void clearWeatherData() {
        weatherLabel.setText("Weather:");
        temperatureLabel.setText("Temperature:");
        humidityLabel.setText("Humidity:");
        windSpeedLabel.setText("Wind Speed:");
        weatherLabel.setForeground(Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WeatherApp::new);
    }
}
