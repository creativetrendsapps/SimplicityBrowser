package com.creativetrends.app.simplicity.adapters;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.FileTypeUtils;
import com.creativetrends.app.simplicity.utils.FileUtils;
import com.creativetrends.simplicity.app.R;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AdapterDownloads extends RecyclerView.Adapter<AdapterDownloads.ViewHolder> {
    private Context context;
    private ArrayList<Files> filesList;

    public AdapterDownloads(Context context, ArrayList<Files> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @NonNull
    @Override
    public AdapterDownloads.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downloads, parent, false);
        return new AdapterDownloads.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterDownloads.ViewHolder holder, final int position) {
        final Files files = filesList.get(position);
        final Uri uri = Uri.parse(files.getUri().toString());
        final File file = new File(uri.getPath());
        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(file);
        holder.mFileImage.setImageResource(fileType.getIcon());
        holder.mFileTitle.setText(file.getName());
        holder.mSize.setText(context.getString(R.string.item_download_details, FileUtils.getReadableFileSize(file.length()), FilenameUtils.getExtension(file.getName())));
        holder.mFileSubtitle.setText(new SimpleDateFormat("E - MMM d, yyyy h:mm a", Locale.getDefault()).format(new Date(file.lastModified())));

        holder.mFile_Holder.setOnClickListener(v -> handleFileClicked(file));
        holder.mDelete.setOnClickListener(v -> {
            final String path = filesList.get(position).getPath();
            final File file1 = new File(path);
            AlertDialog.Builder delete = new AlertDialog.Builder(context);
            delete.setTitle(context.getString(R.string.delete));
            delete.setMessage(context.getString(R.string.delete_message, file.getName()));
            delete.setNegativeButton(context.getString(R.string.cancel), null);
            delete.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                try {
                    if (file1.exists()) {
                        boolean del = file1.delete();
                        filesList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, filesList.size());
                        notifyDataSetChanged();
                        Cardbar.snackBar(context, context.getString(R.string.removed_from_downloads, file.getName()), true).show();
                        if (del) {
                            MediaScannerConnection.scanFile(context, new String[]{path, path}, null, (path1, uri1) -> {
                            });
                        }
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            delete.show();
        });
    }

    private void handleFileClicked(final File clickedFile) {
        try {
            Uri files = FileProvider.getUriForFile(context, context.getString(R.string.auth), clickedFile);
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            newIntent.setDataAndType(files, getMimeType(files));
            newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Can't open file", Toast.LENGTH_LONG).show();
        } catch (Exception p) {
            p.printStackTrace();
            Toast.makeText(context, "Can't open file", Toast.LENGTH_LONG).show();
        }
    }

    private String getMimeType(Uri uri) {
        String mimeType;
        if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mFileImage, mDelete;
        private TextView mFileTitle;
        private TextView mFileSubtitle;
        private TextView mSize;
        private RelativeLayout mFile_Holder;
        ViewHolder(View itemView) {
            super(itemView);
            mFileImage = itemView.findViewById(R.id.item_file_image);
            mFileTitle = itemView.findViewById(R.id.item_file_title);
            mFileSubtitle = itemView.findViewById(R.id.item_file_subtitle);
            mSize = itemView.findViewById(R.id.item_file_size);
            mDelete = itemView.findViewById(R.id.file_delete);
            mFile_Holder = itemView.findViewById(R.id.file_holder);
        }
    }
}
