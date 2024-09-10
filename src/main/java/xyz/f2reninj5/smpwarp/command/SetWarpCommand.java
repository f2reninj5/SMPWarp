package xyz.f2reninj5.smpwarp.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import xyz.f2reninj5.smpwarp.SMPWarp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetWarpCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length != 1) {
            return;
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", args[0]);
        dataMap.put("world-id", String.valueOf(stack.getLocation().getWorld().getUID()));
        dataMap.put("world-name", stack.getLocation().getWorld().getName());
        dataMap.put("x", stack.getLocation().getX());
        dataMap.put("y", stack.getLocation().getY());
        dataMap.put("z", stack.getLocation().getZ());
        dataMap.put("yaw", stack.getLocation().getYaw());
        dataMap.put("pitch", stack.getLocation().getPitch());
        dataMap.put("owner", Objects.requireNonNull(stack.getExecutor()).getUniqueId().toString());

        PrintWriter writer = null;
        try {
            File file = new File(SMPWarp.getPlugin().getDataFolder().getAbsoluteFile() + "/warp", args[0] + ".yml");
            file.getParentFile().mkdir();
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Yaml yaml = new Yaml();
        yaml.dump(dataMap, writer);
    }
}
