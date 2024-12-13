package com.kengine.map.tiled

import com.kengine.graphics.Sprite
import com.kengine.graphics.SpriteSheet
import com.kengine.log.Logging
import com.kengine.math.IntVec2
import com.kengine.math.Vec2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TiledMap(
    val width: Int,
    val height: Int,
    @SerialName("tilewidth")
    val tileWidth: Int,
    @SerialName("tileheight")
    val tileHeight: Int,
    val layers: List<Layer>,
    val tilesets: List<TilesetReference>,
    val orientation: String,
    @SerialName("renderorder")
    val renderOrder: String,
    val infinite: Boolean = false,
) : Logging {

    val p by lazy { Vec2() }

    private data class TilesetAndSpriteSheet(
        val tileset: TilesetReference,
        val spriteSheet: SpriteSheet
    )

    private val tilesetsWithSprites: List<TilesetAndSpriteSheet> by lazy {
        tilesets.map { tileset ->
            TilesetAndSpriteSheet(
                tileset = tileset,
                spriteSheet = SpriteSheet.fromSprite(
                    sprite = Sprite.fromFilePath(adjustAssetPath("assets", tileset.image!!)),
                    tileWidth = tileset.tileWidth!!,
                    tileHeight = tileset.tileHeight!!
                )
            )
        }
    }

    init {
        logger
            .infoStream()
            .writeLn { "Loading Map." }
            .writeLn { "Map: ${width}x${height}" }
            .writeLn { "Tile Dim: ${tileWidth}x${tileHeight}" }
            .writeLn { "Layers: ${layers.size}" }
            .writeLn(layers)
            .writeLn { "Tilesets: ${tilesets.size}" }
            .writeLn(tilesets)
            .flush()
    }

    fun draw() {
        layers.forEach { draw(it) }
    }

    fun draw(layer: Layer) {
        if (!layer.visible) return
        if (layer.type != "tilelayer") return
        if (layer.width == null || layer.height == null) return

        for (x in 0 until layer.width) {
            for (y in 0 until layer.height) {
                val tileId = layer.getTileAt(x, y)
                if (tileId > 0) {
                    val tilesetWithSprite = findTilesetForGid(tileId)
                    val tilePosition = getTilePosition(tileId, tilesetWithSprite.tileset)
                    val sprite = tilesetWithSprite.spriteSheet.getTile(
                        tilePosition.x / tileWidth,
                        tilePosition.y / tileHeight
                    )
                    sprite.draw(p.x + (x * tileWidth).toDouble(), p.y + (y * tileHeight).toDouble())
                }
            }
        }
    }

    private fun findTilesetForGid(gid: Int): TilesetAndSpriteSheet {
        return tilesetsWithSprites.findLast { it.tileset.firstgid <= gid }
            ?: throw IllegalStateException("No tileset found for gid: $gid")
    }

    private fun getTilePosition(tileId: Int, tileset: TilesetReference): IntVec2 {
        val localId = tileId - tileset.firstgid
        val tileX = (localId % tileset.columns!!) * (tileset.tileWidth!! + tileset.spacing) + tileset.margin
        val tileY = (localId / tileset.columns!!) * (tileset.tileHeight!! + tileset.spacing) + tileset.margin
        return IntVec2(tileX, tileY)
    }

    fun adjustAssetPath(basePath: String, relativePath: String): String {
        val baseDir = basePath.substringBeforeLast("/")
        return "$baseDir/$relativePath".replace("../", "")
    }

    override fun toString(): String {
        return "TiledMap(\n" +
                "width=$width, height=$height,\n\t" +
                "tileWidth=$tileWidth, tileHeight=$tileHeight,\n\t" +
                "layers=\n\t\t${layers.joinToString("\n\t\t")},\n\t" +
                "tilesets=\n\t\t${tilesets.joinToString("\n\t\t")},\n\t" +
                "orientation='$orientation',\n\t" +
                "renderOrder='$renderOrder',\n\t" +
                "infinite=$infinite\n" +
                ")"
    }
}

