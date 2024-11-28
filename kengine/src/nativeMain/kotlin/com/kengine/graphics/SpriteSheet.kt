package com.kengine.graphics

import com.kengine.context.useContext
import com.kengine.log.Logger
import com.kengine.math.IntRect

class SpriteSheet private constructor(
    private val texture: Texture,
    val tileWidth: Int,
    val tileHeight: Int,
    private val offsetX: Int = 0,
    private val offsetY: Int = 0,
    private val dx: Int = 0, // spacing between tiles
    private val dy: Int = 0  // spacing between tiles
) {
    val width = texture.width / tileWidth
    val height = texture.height / tileHeight

    /**
     * Get a specific tile as a ClippedSprite based on its grid position (x, y).
     */
    fun getTile(x: Int, y: Int): Sprite {
        val clip = IntRect(
            x = offsetX + x * (tileWidth + dx),
            y = offsetY + y * (tileHeight + dy),
            w = tileWidth,
            h = tileHeight
        )
        Logger.debug { "Loading tile from ($x,$y) -> [(${clip.x},${clip.y}) ${clip.w}${clip.h}" }
        return Sprite.fromTexture(texture, clip)
    }

    fun cleanup() {
    }

    companion object {
        fun fromFilePath(
            filePath: String,
            tileWidth: Int,
            tileHeight: Int,
            offsetX: Int = 0,
            offsetY: Int = 0,
            dx: Int = 0, // spacing between tiles
            dy: Int = 0  // spacing between tiles
        ): SpriteSheet {
            return useContext(TextureContext.get()) {
                val texture = getTexture(filePath)
                SpriteSheet(texture, tileWidth, tileHeight, offsetX, offsetY, dx, dy)
            }
        }
        fun fromTexture(
            texture: Texture,
            tileWidth: Int,
            tileHeight: Int,
            offsetX: Int = 0,
            offsetY: Int = 0,
            dx: Int = 0, // spacing between tiles
            dy: Int = 0  // spacing between tiles
        ): SpriteSheet {
            return useContext(TextureContext.get()) {
                SpriteSheet(texture, tileWidth, tileHeight, offsetX, offsetY, dx, dy)
            }
        }
        fun fromSprite(
            sprite: Sprite,
            tileWidth: Int,
            tileHeight: Int,
            offsetX: Int = 0,
            offsetY: Int = 0,
            dx: Int = 0, // spacing between tiles
            dy: Int = 0  // spacing between tiles
        ): SpriteSheet {
            return useContext(TextureContext.get()) {
                SpriteSheet(sprite.texture, tileWidth, tileHeight, offsetX, offsetY, dx, dy)
            }
        }
    }

}
