package app.revanced.patches.youtube.utils.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [
        Package(
            "com.google.android.youtube",
            arrayOf(
                "18.19.36",
                "18.20.34",
                "18.20.35",
                "18.20.36",
                "18.20.39",
                "18.21.34",
                "18.21.35",
                "18.22.35",
                "18.22.36",
                "18.22.37",
                "18.23.35",
                "18.23.36",
                "18.24.36",
                "18.24.37",
                "18.25.39",
                "18.25.40",
                "18.27.33",
                "18.27.34",
                "18.27.35",
                "18.27.36",
                "18.29.33",
                "18.29.38",
                "18.30.36",
                "18.30.37",
                "18.31.37",
                "18.31.38",
                "18.31.40",
                "18.32.36",
                "18.32.39",
                "18.33.40",
                "18.34.37",
                "18.34.38",
                "18.35.35"
            )
        )
    ]
)
@Target(AnnotationTarget.CLASS)
internal annotation class YouTubeCompatibility

