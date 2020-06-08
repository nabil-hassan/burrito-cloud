delete from order_burritos;
delete from orders;
delete from burrito_ingredients;
delete from burrito;
delete from ingredient;

insert into ingredient(type, id, name)
values('WRAP', 'FLTO', 'Flour Tortilla');

insert into ingredient(type, id, name)
values('WRAP', 'COTO', 'Corn Tortilla');

insert into ingredient(type, id, name)
values('MEAT', 'CHCK', 'Chicken');

insert into ingredient(type, id, name)
values('MEAT', 'BSTK', 'Steak');

insert into ingredient(type, id, name)
values('MEAT', 'TRKY', 'Turkey');

insert into ingredient(type, id, name)
values('VEGETABLE', 'TOMO', 'Tomato');

insert into ingredient(type, id, name)
values('VEGETABLE', 'ONIN', 'Onion');

insert into ingredient(type, id, name)
values('VEGETABLE', 'PPR', 'Pepper');

insert into ingredient(type, id, name)
values('SAUCE', 'KTCH', 'Ketchup');

insert into ingredient(type, id, name)
values('SAUCE', 'MAYO', 'Mayonaise');