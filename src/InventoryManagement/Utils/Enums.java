package InventoryManagement.Utils;

public class Enums
{
    public enum SearchFilter
    {
        Id("By Id"),
        Name("By Name");

        private String value;

        SearchFilter(String value)
        {
            this.value = value;
        }

        public String toString()
        {
            return this.value;
        }
    }

    public enum SortColumn
    {
        None,
        SortByIdAsc,
        SortByIdDesc,
        SortByNameAsc,
        SortByNameDesc,
        SortByInvAsc,
        SortByInvDesc,
        SortByPriceAsc,
        SortByPriceDesc
    }

    public enum EntityState
    {
        Unchanged,
        Deleted,
        Modified,
        Added
    }
}
