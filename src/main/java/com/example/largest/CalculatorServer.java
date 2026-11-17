package com.example.largest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** JDK-only HTTP sample app for container and Kubernetes deployment. */
public class CalculatorServer {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", CalculatorServer::handle);
        server.start();
        System.out.println("Largest-three calculator listening on port " + port);
    }

    private static void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/health".equals(path)) {
            send(exchange, 200, "text/plain; charset=utf-8", "OK");
            return;
        }
        if ("/largest".equals(path)) {
            try {
                Map<String, String> q = query(exchange.getRequestURI().getRawQuery());
                double a = Double.parseDouble(q.get("a"));
                double b = Double.parseDouble(q.get("b"));
                double c = Double.parseDouble(q.get("c"));
                double largest = LargestOfThree.largest(a, b, c);
                String json = String.format(java.util.Locale.ROOT,
                    "{\"a\":%s,\"b\":%s,\"c\":%s,\"largest\":%s}", a, b, c, largest);
                send(exchange, 200, "application/json; charset=utf-8", json);
            } catch (Exception ex) {
                send(exchange, 400, "application/json; charset=utf-8",
                    "{\"error\":\"Provide numeric query parameters a, b and c.\"}");
            }
            return;
        }

        String html = """
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Largest of Three Numbers</title>
              <style>
                body{font-family:Arial,sans-serif;background:#f3f6fb;margin:0;min-height:100vh;display:grid;place-items:center}
                main{background:white;padding:32px;border-radius:14px;box-shadow:0 8px 30px #1b31501a;width:min(90%,380px)}
                h1{font-size:24px;color:#172b4d}label{display:block;margin:14px 0 6px}
                input{box-sizing:border-box;width:100%;padding:11px;border:1px solid #ccd5e0;border-radius:7px;font-size:16px}
                button{margin-top:20px;width:100%;padding:12px;background:#185abc;color:white;border:0;border-radius:7px;font-size:16px;cursor:pointer}
                #result{font-weight:bold;margin-top:18px;color:#12633b}
              </style>
            </head>
            <body><main>
              <h1>Largest of Three Numbers</h1>
              <p>Enter any three numbers to find the largest.</p>
              <form id="calc">
                <label for="a">First number</label><input id="a" type="number" step="any" required>
                <label for="b">Second number</label><input id="b" type="number" step="any" required>
                <label for="c">Third number</label><input id="c" type="number" step="any" required>
                <button type="submit">Find Largest</button>
              </form>
              <p id="result" aria-live="polite"></p>
            </main>
            <script>
              document.getElementById('calc').addEventListener('submit', async e => {
                e.preventDefault();
                const values = ['a','b','c'].map(id => document.getElementById(id).value);
                const response = await fetch('/largest?' + new URLSearchParams({a:values[0],b:values[1],c:values[2]}));
                const data = await response.json();
                document.getElementById('result').textContent =
                  response.ok ? 'Largest number = ' + data.largest : data.error;
              });
            </script></body></html>
            """;
        send(exchange, 200, "text/html; charset=utf-8", html);
    }

    private static Map<String, String> query(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw == null) return result;
        for (String pair : raw.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    private static void send(HttpExchange exchange, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
