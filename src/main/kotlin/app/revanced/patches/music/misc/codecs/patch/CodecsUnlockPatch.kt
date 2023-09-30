package app.revanced.patches.music.misc.codecs.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.patches.shared.patch.opus.AbstractOpusCodecsPatch
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_MISC_PATH

@Patch(
    name = "Enable opus codec",
    description = "Enable opus codec when playing audio.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.apps.youtube.music",
            [
                "6.15.52",
                "6.20.51",
                "6.21.51"
            ]
        )
    ]
    dependencies = [SettingsPatch::class]
)
@Suppress("unused")
object CodecsUnlockPatch : AbstractOpusCodecsPatch(
    "$MUSIC_MISC_PATH/OpusCodecPatch;->enableOpusCodec()Z"
) {
    override fun execute(context: BytecodeContext) {
        super.execute(context)

        SettingsPatch.addMusicPreference(
            CategoryType.MISC,
            "revanced_enable_opus_codec",
            "true"
        )

    }
}
