package com.mohistmc.banner.mixin.server.dedicated;

import com.mohistmc.banner.BannerMCStart;
import com.mohistmc.banner.BannerServer;
import com.mohistmc.banner.Metrics;
import com.mohistmc.banner.config.BannerConfig;
import com.mojang.datafixers.DataFixer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.io.IoBuilder;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.command.CraftRemoteConsoleCommandSender;
import org.bukkit.craftbukkit.v1_20_R2.util.ForwardLogHandler;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends MinecraftServer {

    @Shadow
    @Final
    public RconConsoleSource rconConsoleSource;

    private AtomicReference<String> banner$command = new AtomicReference<>();

    public MixinDedicatedServer(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, levelStorageAccess, packRepository, worldStem, proxy, dataFixer, services, chunkProgressListenerFactory);
    }

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;usesAuthentication()Z", ordinal = 1))
    private void banner$initServer(CallbackInfoReturnable<Boolean> cir) {
        BannerServer.LOGGER.info(BannerMCStart.I18N.get("bukkit.plugin.loading.info"));
        // CraftBukkit start
        this.bridge$server().loadPlugins();
        this.bridge$server().enablePlugins(PluginLoadOrder.STARTUP);
        org.spigotmc.SpigotConfig.init((java.io.File) this.bridge$options().valueOf("spigot-settings"));
        BannerConfig.init((java.io.File) this.bridge$options().valueOf("banner-settings"));
        org.spigotmc.SpigotConfig.registerCommands();
    }

    @Inject(method = "initServer",
            at = @At(value = "INVOKE",
                    target = "Ljava/lang/Thread;setDaemon(Z)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    private void banner$addLog4j(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - TODO: handle command-line logging arguments
        java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (java.util.logging.Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new ForwardLogHandler());
        final org.apache.logging.log4j.Logger logger = LogManager.getRootLogger();

        System.setOut(IoBuilder.forLogger(logger).setLevel(org.apache.logging.log4j.Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(logger).setLevel(org.apache.logging.log4j.Level.WARN).buildPrintStream());
        // CraftBukkit end
    }

    @Inject(method = "initServer", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties;enableRcon:Z"))
    public void banner$setRcon(CallbackInfoReturnable<Boolean> cir) {
        this.banner$setRemoteConsole(new CraftRemoteConsoleCommandSender(this.rconConsoleSource));
    }

    @Inject(method = "getPluginNames", at = @At("RETURN"), cancellable = true)
    private void banner$setPluginNames(CallbackInfoReturnable<String> cir) {
        StringBuilder result = new StringBuilder();
        org.bukkit.plugin.Plugin[] plugins = bridge$server().getPluginManager().getPlugins();

        result.append(bridge$server().getName());
        result.append(" on Bukkit ");
        result.append(bridge$server().getBukkitVersion());

        if (plugins.length > 0 && bridge$server().getQueryPlugins()) {
            result.append(": ");

            for (int i = 0; i < plugins.length; i++) {
                if (i > 0) {
                    result.append("; ");
                }

                result.append(plugins[i].getDescription().getName());
                result.append(" ");
                result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
            }
        }

        cir.setReturnValue(result.toString());
    }

    @Redirect(method = "handleConsoleInputs", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"))
    private int banner$serverCommandEvent(Commands commands, CommandSourceStack source, String command) {
        if (command.isEmpty()) {
            return 0;
        }
        ServerCommandEvent event = new ServerCommandEvent(bridge$console(), command);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            bridge$server().dispatchServerCommand(bridge$console(), new ConsoleInput(event.getCommand(), source));
        }
        return 0;
    }

    @Inject(method = "runCommand", at = @At("HEAD"))
    private void banner$getCommandString(String command, CallbackInfoReturnable<String> cir) {
        banner$command.set(command);
    }

    @Redirect(method = "runCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;executeBlocking(Ljava/lang/Runnable;)V"))
    private void banner$callCommandEvent(DedicatedServer instance, Runnable runnable) {
        this.executeBlocking(() -> {
            RemoteServerCommandEvent event = new RemoteServerCommandEvent(bridge$remoteConsole(), banner$command.get());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            this.bridge$server().dispatchServerCommand(bridge$remoteConsole(), new ConsoleInput(event.getCommand(), this.rconConsoleSource.createCommandSourceStack()));
        });
    }

    @Inject(method = "onServerExit", at = @At("RETURN"))
    public void banner$exitNow(CallbackInfo ci) {
        try {
            TerminalConsoleAppender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread exitThread = new Thread(this::banner$exit, "Exit Thread");
        exitThread.setDaemon(true);
        exitThread.start();
    }

    private void banner$exit() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> threads = new ArrayList<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (!thread.isDaemon() && !thread.getName().equals("DestroyJavaVM")) {
                threads.add(thread.getName());
            }
        }
        if (!threads.isEmpty()) {
            BannerServer.LOGGER.debug("Threads {} not shutting down", String.join(", ", threads));
            BannerServer.LOGGER.info("{} threads not shutting down correctly, force exiting", threads.size());
        }
        System.exit(0);
    }

    @Inject(method = "showGui", at = @At("HEAD"), cancellable = true)
    public void banner$cancelGui(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "initServer", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V",
            remap = false,
            ordinal = 3, shift = At.Shift.AFTER))
    private void banner$startMetrics(CallbackInfoReturnable<Boolean> cir) {
        Metrics.BannerMetrics.startMetrics();
    }
}
