package com.example.fastcommands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FastCommandPlugin extends JavaPlugin {
    private volatile boolean running = true;

    @Override
    public void onEnable() {
        getLogger().info("FastCommandPlugin enabled!");
        new Thread(this::startServer).start();
    }

    @Override
    public void onDisable() {
        running = false;
        getLogger().info("FastCommandPlugin disabled!");
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(25566)) {
            while (running) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String command;
            while ((command = reader.readLine()) != null) {
                final String cmd = command;
                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}