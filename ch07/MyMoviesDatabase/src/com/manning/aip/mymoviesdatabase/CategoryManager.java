package com.manning.aip.mymoviesdatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.manning.aip.mymoviesdatabase.model.Category;

import java.util.Collections;
import java.util.List;

public class CategoryManager extends Activity {

   private static final int CONTEXT_MENU_DELETE = 0;

   private MyMoviesApp app;

   private List<Category> categories;
   private ArrayAdapter<Category> adapter;

   private ListView listView;

   private Button categoryAddShowDialog;
   private Dialog categoryAddDialog;
   private EditText categoryAdd;
   private Button categoryAddSubmit;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.category_manager);

      app = (MyMoviesApp) getApplication();

      categories = app.getDataManager().getAllCategories();
      listView = (ListView) this.findViewById(R.id.category_manager_list);
      listView.setEmptyView(findViewById(R.id.category_manager_list_empty));
      adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, categories);
      listView.setAdapter(adapter);
      registerForContextMenu(listView);

      categoryAddShowDialog = (Button) findViewById(R.id.category_add_show_dialog_button);
      this.categoryAddShowDialog.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            if (!categoryAddDialog.isShowing()) {
               categoryAddDialog.show();
            }
         }
      });

      categoryAddDialog = new Dialog(this);
      categoryAddDialog.setContentView(R.layout.category_add_dialog);
      categoryAddDialog.setTitle("   Add New Category");
      categoryAdd = (EditText) categoryAddDialog.findViewById(R.id.category_add);
      categoryAddSubmit = (Button) categoryAddDialog.findViewById(R.id.category_add_submit);
      categoryAddSubmit.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            if (!isTextViewEmpty(categoryAdd)) {
               Category exists = app.getDataManager().findCategory(categoryAdd.getText().toString());
               if (exists == null) {
                  Category category = new Category(0, categoryAdd.getText().toString());
                  app.getDataManager().saveCategory(category);
                  // we could just ADD to adapter, and not backing collection
                  // but that will put element at end of ListView, here we want to add and sort
                  categories.add(category);
                  Collections.sort(categories);
                  adapter.notifyDataSetChanged();
               } else {
                  Toast.makeText(CategoryManager.this, "Category already exists", Toast.LENGTH_SHORT).show();
               }
            }
            // cancel vs dismiss vs hide
            categoryAddDialog.cancel();
         }
      });
   }

   @Override
   public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.add(0, CategoryManager.CONTEXT_MENU_DELETE, 0, "Delete Category");
      menu.setHeaderTitle("Action");
   }

   @Override
   public boolean onContextItemSelected(final MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      final Category category = categories.get(info.position);
      switch (item.getItemId()) {
         case CONTEXT_MENU_DELETE:
            new AlertDialog.Builder(CategoryManager.this).setTitle("Delete Category?").setMessage(category.getName())
                     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface d, final int i) {
                           app.getDataManager().deleteCategory(category);
                           adapter.remove(category);
                        }
                     }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface d, final int i) {
                        }
                     }).show();
            return true;
         default:
            return super.onContextItemSelected(item);
      }
   }

   private boolean isTextViewEmpty(final TextView textView) {
      return !((textView != null) && (textView.getText() != null) && (textView.getText().toString() != null) && !textView
               .getText().toString().equals(""));
   }
}
