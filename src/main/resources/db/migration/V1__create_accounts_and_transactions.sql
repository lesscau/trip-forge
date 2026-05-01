create table accounts (
    id uuid primary key,
    name varchar(120) not null,
    created_at timestamptz not null
);

create table ledger_transactions (
    id uuid primary key,
    title varchar(200) not null,
    amount numeric(19, 2) not null,
    currency varchar(3) not null,
    paid_by_account_id uuid not null references accounts(id),
    transaction_date date not null,
    created_at timestamptz not null
);

create table transaction_participants (
    transaction_id uuid not null references ledger_transactions(id) on delete cascade,
    account_id uuid not null references accounts(id),
    share_amount numeric(19, 2) not null,
    primary key (transaction_id, account_id)
);

create index idx_ledger_transactions_paid_by on ledger_transactions(paid_by_account_id);
create index idx_transaction_participants_account on transaction_participants(account_id);
