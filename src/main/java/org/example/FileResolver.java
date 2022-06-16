package org.example;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface FileResolver {

    /**
     * HTTPで受信したパスから相当するファイルのFileクラスのインスタンスを見つける
     *
     * <p>
     * パスをマップするのみでファイルの存在まではチェックしていない。
     * </p>
     * @param path
     * @return 送信するファイルを返す、ファイルがない場合は null を返す
     */
    @Nullable
    File findFile(String path);
}
