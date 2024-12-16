package com.kengine.graphics

import com.kengine.hooks.context.getContext
import com.kengine.log.Logging
import com.kengine.math.IntRect

class SpriteSheet private constructor(
    private val texture: Texture,
    val tileWidth: Int,
    val tileHeight: Int,
    val marginX: Int = 0,
    val marginY: Int = 0,
    val spacingX: Int = 0,
    val spacingY: Int = 0
) : Logging {
    val width = texture.width - (2 * marginX)
    val height = texture.height - (2 * marginY)
    val columns = (width + spacingX) / (tileWidth + spacingX)
    val rows = (height + spacingY) / (tileHeight + spacingY)

    // Cache of pre-created sprites
    private val sprites: Array<Array<Sprite>> = Array(rows) { y ->
        Array(columns) { x ->
            val pixelX = marginX + x * (tileWidth + spacingX)
            val pixelY = marginY + y * (tileHeight + spacingY)
            Sprite.fromTexture(
                texture,
                IntRect(x = pixelX, y = pixelY, w = tileWidth, h = tileHeight)
            )
        }
    }

    init {
        logger.infoStream {
            write("SpriteSheet initialized:")
            write("tile size ${tileWidth}x$tileHeight, ")
            write("margin ($marginX,$marginY), ")
            write("spacing ($spacingX,$spacingY), ")
            write("cached ${rows * columns} sprites")
        }
    }

    fun getTile(x: Int, y: Int): Sprite {
        require(x in 0 until columns && y in 0 until rows) {
            "Tile coordinates ($x,$y) out of bounds. Sheet size: ${columns}x$rows"
        }

        logger.trace {
            val pixelX = marginX + x * (tileWidth + spacingX)
            val pixelY = marginY + y * (tileHeight + spacingY)
            "getTile($x,$y) -> pixel($pixelX,$pixelY), tileSize=${tileWidth}x${tileHeight}, " +
                    "textureSize=${texture.width}x${texture.height}, margin=($marginX,$marginY), spacing=($spacingX,$spacingY)"
        }

        return sprites[y][x]
    }

    fun cleanup() {
        sprites.forEach { row ->
            row.forEach { sprite ->
                sprite.cleanup()
            }
        }
    }

    companion object {
        fun fromFilePath(
            filePath: String,
            tileWidth: Int,
            tileHeight: Int,
            offsetX: Int = 0,
            offsetY: Int = 0,
            paddingX: Int = 0,
            paddingY: Int = 0
        ): SpriteSheet {
            val texture = getContext<TextureContext>().getTexture(filePath)
            return SpriteSheet(texture, tileWidth, tileHeight, offsetX, offsetY, paddingX, paddingY)
        }

        fun fromTexture(
            texture: Texture,
            tileWidth: Int,
            tileHeight: Int,
            offsetX: Int = 0,
            offsetY: Int = 0,
            paddingX: Int = 0,
            paddingY: Int = 0
        ): SpriteSheet {
            return SpriteSheet(texture, tileWidth, tileHeight, offsetX, offsetY, paddingX, paddingY)
        }

        fun fromSprite(
            sprite: Sprite,
            tileWidth: Int,
            tileHeight: Int,
            offsetX: Int = 0,
            offsetY: Int = 0,
            paddingX: Int = 0,
            paddingY: Int = 0
        ): SpriteSheet {
            return SpriteSheet(sprite.texture, tileWidth, tileHeight, offsetX, offsetY, paddingX, paddingY)
        }
    }

}
