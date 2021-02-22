DO $$ 
    BEGIN
    
        create table if not exists /*your_schema.*/thread_threadtableprops (
            userid bigint not null,
            columnid text not null,
            show_ boolean,
            sorttype integer,
            width integer,
            num integer,
            constraint thread_threadtableprops_pkey primary key (userid, columnid)
        )
        with (
            oids=false
        );

        create table if not exists /*your_schema.*/thread_threadtablesettings (
          userid bigint not null,
          pagesize integer,
          constraint thread_threadtablesettings_pkey primary key (userid)
        )
        with (
          oids=false
        );

    END;
$$