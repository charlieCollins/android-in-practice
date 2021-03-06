package com.manning.aip.mymoviesdatabase.model;

public class Category extends ModelBase implements Comparable<Category> {

   // NOTE in real-world android app you might want a CategoryFactory
   // or factory method here, to cut down on number of objects created
   // (there will only ever be a small number of categories, and they are simple, 
   // no need to recreate objects over again that represent same String)  

   private String name;

   public Category() {
   }

   public Category(long id, String name) {
      this.id = id;
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public int compareTo(Category another) {
      if (another == null) {
         return -1;
      }
      if (this.name == null) {
         return 1;
      }
      return this.name.compareTo(another.name);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      // upper name so hashCode is consistent with equals (equals ignores case)
      result = prime * result + ((this.name == null) ? 0 : this.name.toUpperCase().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (!(obj instanceof Category)) {
         return false;
      }
      Category other = (Category) obj;
      if (this.name == null) {
         if (other.name != null) {
            return false;
            // name check ignores case
         }
      } else if (!this.name.equalsIgnoreCase(other.name)) {
         return false;
      }
      return true;
   }
}
