package com.bluewhaleyt.chatgpt.ui.activities;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import com.bluewhaleyt.chatgpt.databinding.ActivityCodeViewBinding;
import com.bluewhaleyt.chatgpt.utils.Constants;
import com.bluewhaleyt.codeeditor.textmate.syntaxhighlight.SyntaxHighlightUtil;
import com.bluewhaleyt.common.CommonUtil;
import com.bluewhaleyt.common.IntentUtil;
import com.bluewhaleyt.component.snackbar.SnackbarUtil;
import com.bluewhaleyt.filemanagement.FileUtil;

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula;

public class CodeViewActivity extends BaseActivity {

    private ActivityCodeViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            initialize();
        } catch (Exception e) {
            SnackbarUtil.makeErrorSnackbar(this, e.getMessage());
        }
    }

    private void initialize() throws Exception {
        var path = IntentUtil.intentGetString(this, "file_path");

        getSupportActionBar().setTitle(FileUtil.getFileNameOfPath(path));
        getSupportActionBar().setSubtitle(path);
        binding.codeView.setText(FileUtil.readFile(path));
        binding.codeView.setEditable(false);

        var font = Typeface.createFromAsset(getAssets(), Constants.CODE_FONT);
        binding.codeView.setTypefaceText(font);
        binding.codeView.setTypefaceLineNumber(font);

        var oneLight = "OneLight.json";
        var oneDark = "OneDark.json";

        var syntaxHighlight = new SyntaxHighlightUtil();
        syntaxHighlight.setLanguageBase("languages.json");
        syntaxHighlight.setLanguageDirectory(Constants.LANGUAGE_DIR);
        syntaxHighlight.setThemeDirectory(Constants.THEME_DIR);

        var editorTheme = CommonUtil.isInDarkMode(this) ? oneDark : oneLight;
        String[] themes = {oneDark, oneLight};
        syntaxHighlight.setThemes(themes);
        syntaxHighlight.setTheme(editorTheme);

        syntaxHighlight.setup(this, binding.codeView, path);

    }

}