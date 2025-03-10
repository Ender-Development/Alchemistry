package al132.alchemistry.client

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.ResourceLocation

class AbbreviationRenderer(
    gameSettingsIn: GameSettings, location: ResourceLocation, textureManagerIn: TextureManager,
    unicode: Boolean
) : FontRenderer(
    gameSettingsIn,
    location, textureManagerIn, unicode
) {
    override fun renderDefaultChar(ch: Int, italic: Boolean): Float {
        val i: Int = ch % 16 * 8
        val j: Int = ch / 16 * 8
        val k: Int = if(italic) { 1 } else { 0 }
        bindTexture(this.locationFontTexture)
        val l: Int = this.charWidth[ch]
        val f: Float = l - 0.01F
        GlStateManager.glBegin(5)
        GlStateManager.glTexCoord2f(i / 128.0F, j / 128.0F)
        GlStateManager.glVertex3f(this.posX + k, this.posY, 0.0F)
        GlStateManager.glTexCoord2f(i / 128.0F, (j + 7.99F) / 128.0F)
        GlStateManager.glVertex3f(this.posX - k, this.posY + 7.99F, 0.0F)
        GlStateManager.glTexCoord2f((i + f - 1.0F) / 128.0F, j / 128.0F)
        GlStateManager.glVertex3f(this.posX + f - 1.0F + k, this.posY, 0.0F)
        GlStateManager.glTexCoord2f((i + f - 1.0F) / 128.0F, (j + 7.99F) / 128.0F)
        GlStateManager.glVertex3f(this.posX + f - 1.0F - k, this.posY + 7.99F, 0.0F)
        GlStateManager.glEnd()
        return l.toFloat()
    }
}