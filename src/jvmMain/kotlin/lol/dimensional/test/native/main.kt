package lol.dimensional.test.native

import com.sedmelluq.discord.lavaplayer.manager.AudioPlayer
import com.sedmelluq.discord.lavaplayer.manager.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.common.SourceRegistry
import com.sedmelluq.discord.lavaplayer.tools.extensions.into
import com.sedmelluq.discord.lavaplayer.tools.extensions.loadItem
import com.sedmelluq.discord.lavaplayer.track.loader.ItemLoadResult
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import dev.minn.jda.ktx.intents
import dev.minn.jda.ktx.light
import dev.minn.jda.ktx.listener
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.factory.DefaultSendSystem
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import java.nio.ByteBuffer
import kotlin.system.exitProcess

val env = System.getenv()

fun main() {
    val jda = light(env["BOT_TOKEN"] ?: error("no bot token"), enableCoroutines = true) {
        intents += listOf(GatewayIntent.GUILD_MESSAGES)

        setAudioSendFactory { packetProvider -> DefaultSendSystem(packetProvider) }
    }

    val manager = DefaultAudioPlayerManager()
    SourceRegistry.registerRemoteSources(manager)

    jda.listener<ReadyEvent> {
        val vc = jda.getVoiceChannelById(942625189911490570L)
            ?: exitProcess(1)

        val player = manager.createPlayer()
        player.volume = 100

        /* connect to voice channel. */
        vc.guild.audioManager.apply {
            openAudioConnection(vc)
            sendingHandler = AudioPlayerSendHandler(player)
            isSelfDeafened = true
        }

        /* load audio track. */
        val track = manager.loadItem("ytsearch:promise u wont get tired of me bixby")
            .into<ItemLoadResult.CollectionLoaded>().collection.tracks
            .first()

        player.playTrack(track)
    }
}

class AudioPlayerSendHandler(val player: AudioPlayer) : AudioSendHandler {
    val buffer = ByteBuffer.allocate(2048)
    val frame = MutableAudioFrame()

    init {
        frame.setBuffer(buffer)
    }

    override fun isOpus(): Boolean = true

    override fun canProvide(): Boolean = player.provide(frame)

    override fun provide20MsAudio(): ByteBuffer? = buffer.flip()
}
