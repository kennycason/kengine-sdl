package com.kengine.entity

import com.kengine.graphics.FlipMode
import com.kengine.graphics.Sprite

open class SpriteEntity(
    protected val sprite: Sprite,
    var flipMode: FlipMode = FlipMode.NONE,
    var rotation: Double = 0.0
) : Entity(
    width = sprite.width,
    height = sprite.height
) {
    override fun update() {}
    override fun draw() {
        sprite.rotation = rotation
        sprite.draw(p.x, p.y, flipMode)
    }
    override fun cleanup() {
        sprite.cleanup()
    }
}
