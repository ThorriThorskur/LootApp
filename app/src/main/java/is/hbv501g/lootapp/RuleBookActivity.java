package is.hbv501g.lootapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleBookActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private List<String> ruleSections;
    private HashMap<String, List<String>> ruleBookMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rule_book);

        ImageButton buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(RuleBookActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        expandableListView = findViewById(R.id.expandableListView);

        // 1) Create the top-level sections.
        ruleSections = new ArrayList<>();
        ruleSections.add("Game");
        ruleSections.add("Turn");
        ruleSections.add("Card Types");
        ruleSections.add("Keywords");

        // 2) Create the child data for each section.
        ruleBookMap = new HashMap<>();

        // --- GAME section
        List<String> gameRules = new ArrayList<>();
        gameRules.add("Object of the Game\n" +
                "Each player aims to reduce their opponents from 20 life to 0, or force them to draw from an empty library, or fulfill another alternate-win condition.");
        gameRules.add("Life Totals\n" +
                "Players typically start at 20 life (some formats may differ). If a player’s life total reaches 0 or less, they lose the game unless another rule says otherwise.");
        gameRules.add("Deck Sizes\n" +
                "In most Constructed formats, a deck must have at least 60 cards. In Limited formats, a deck must have at least 40 cards. There’s no maximum deck size, but you must be able to shuffle your deck without assistance.");
        ruleBookMap.put("Game", gameRules);

        // --- TURN section
        List<String> turnRules = new ArrayList<>();
        turnRules.add(
                "1. Beginning Phase\n" +
                        "Untap step\n" +
                        "You untap all your tapped permanents. On the first turn, you typically have no permanents, so you skip this. " +
                        "No one can cast spells or activate abilities in this step.\n\n" +
                        "Upkeep step\n" +
                        "Abilities that trigger 'at the beginning of your upkeep' go on the stack. " +
                        "Players can cast instants and activate abilities.\n\n" +
                        "Draw step\n" +
                        "You draw a card from your library. (The player who goes first skips this draw on their first turn.) " +
                        "Players can then cast instants and activate abilities."
        );
        turnRules.add(
                "2. Main Phase\n" +
                        "You can cast any number of sorceries, creatures, artifacts, enchantments, planeswalkers, and activate abilities. " +
                        "You can also play one land if you haven’t yet this turn. " +
                        "Your opponents can only cast instants or activate abilities (unless they have something that says otherwise)."
        );
        turnRules.add(
                "3. Combat Phase\n" +
                        "Beginning of Combat Step\n" +
                        "Players can cast instants and activate abilities. This is the last chance for an opponent to prevent creatures from attacking.\n\n" +
                        "Declare Attackers Step\n" +
                        "You choose which of your untapped creatures will attack and who or what they will attack. This taps them. " +
                        "Players can then cast instants and activate abilities.\n\n" +
                        "Declare Blockers Step\n" +
                        "Your opponent decides which of their creatures will block your attacking creatures (if any). If multiple creatures block one attacker, " +
                        "order them for damage assignment. Players can cast instants and activate abilities.\n\n" +
                        "Combat Damage Step\n" +
                        "All attacking and blocking creatures deal damage simultaneously (unless they have first strike or double strike). " +
                        "If an attacking creature was blocked by multiple creatures, distribute its damage in the order you chose. " +
                        "Then players may cast instants and activate abilities.\n\n" +
                        "End of Combat Step\n" +
                        "Players can cast instants and activate abilities after damage is dealt but before the combat phase ends."
        );
        turnRules.add(
                "4. Main Phase (Second)\n" +
                        "This phase is like your first main phase. You can cast sorceries or other spells that require a main phase and play a land if you haven’t already."
        );
        turnRules.add(
                "5. Ending Phase\n" +
                        "End Step\n" +
                        "Abilities that trigger 'at the beginning of your end step' go on the stack. Players may cast instants and activate abilities.\n\n" +
                        "Cleanup Step\n" +
                        "If you have more than seven cards in hand, you discard down to seven. All damage is removed from creatures, and 'until end of turn' effects end. " +
                        "No one can cast spells or activate abilities unless an ability triggers during this step."
        );
        ruleBookMap.put("Turn", turnRules);

        // --- CARD TYPES section
        List<String> cardTypes = new ArrayList<>();
        cardTypes.add(
                "Sorcery\n" +
                        "A spell you can cast only during your main phase when the stack is empty. Typically more powerful or complex effects."
        );
        cardTypes.add(
                "Instant\n" +
                        "A spell you can cast any time you have priority, even during combat or on your opponent’s turn."
        );
        cardTypes.add(
                "Creature\n" +
                        "A permanent that can attack and block. Creatures have power (damage dealt) and toughness (damage required to destroy them)."
        );
        cardTypes.add(
                "Artifact\n" +
                        "A permanent representing a magical object. Usually colorless, and can have a wide range of effects."
        );
        cardTypes.add(
                "Planeswalker\n" +
                        "A powerful permanent that can activate one loyalty ability each of your turns. Opponents can attack it to reduce its loyalty."
        );
        ruleBookMap.put("Card Types", cardTypes);

        // --- KEYWORDS section
        List<String> keywords = new ArrayList<>();

        keywords.add(
                "Haste\n" +
                        "A creature with haste can attack and use activated abilities with the tap symbol on the same turn it enters the battlefield.\n" +
                        "----------------------"
        );

        keywords.add(
                "Flying\n" +
                        "A creature with flying can only be blocked by creatures that have flying or reach.\n" +
                        "----------------------"
        );

        keywords.add(
                "Deathtouch\n" +
                        "Any amount of damage dealt by a creature with deathtouch is enough to destroy the creature it deals damage to.\n" +
                        "----------------------"
        );

        keywords.add(
                "Lifelink\n" +
                        "Damage dealt by a creature or spell with lifelink also causes its controller to gain that much life.\n" +
                        "----------------------"
        );

        keywords.add(
                "First Strike\n" +
                        "A creature with first strike deals combat damage before creatures without first strike.\n" +
                        "----------------------"
        );

        keywords.add(
                "Double Strike\n" +
                        "A creature with double strike deals both first-strike damage and regular combat damage.\n" +
                        "----------------------"
        );

        keywords.add(
                "Vigilance\n" +
                        "A creature with vigilance doesn’t tap when it attacks.\n" +
                        "----------------------"
        );

        keywords.add(
                "Trample\n" +
                        "A creature with trample can deal excess combat damage to the player or planeswalker it’s attacking if its blockers have less than lethal toughness.\n" +
                        "----------------------"
        );

        keywords.add(
                "Menace\n" +
                        "A creature with menace can’t be blocked except by two or more creatures.\n" +
                        "----------------------"
        );

        keywords.add(
                "Reach\n" +
                        "A creature with reach can block creatures that have flying.\n" +
                        "----------------------"
        );

        keywords.add(
                "Hexproof\n" +
                        "A permanent or player with hexproof can’t be the target of spells or abilities your opponents control.\n" +
                        "----------------------"
        );

        keywords.add(
                "Ward\n" +
                        "Whenever a permanent with ward becomes the target of a spell or ability an opponent controls, counter it unless that opponent pays a specified cost.\n" +
                        "----------------------"
        );

        keywords.add(
                "Indestructible\n" +
                        "Effects that say “destroy” don’t destroy a permanent with indestructible, and it can’t be destroyed by damage.\n" +
                        "----------------------"
        );

        keywords.add(
                "Flash\n" +
                        "A spell with flash can be cast any time you could cast an instant.\n" +
                        "----------------------"
        );

        keywords.add(
                "Defender\n" +
                        "A creature with defender can’t attack.\n" +
                        "----------------------"
        );

        keywords.add(
                "Shroud\n" +
                        "A permanent or player with shroud can’t be the target of any spells or abilities (including yours).\n" +
                        "----------------------"
        );

        keywords.add(
                "Protection\n" +
                        "A permanent with protection from [quality] can’t be blocked, targeted, dealt damage, or enchanted/equipped by anything of that quality.\n" +
                        "----------------------"
        );

        keywords.add(
                "Equip\n" +
                        "“Equip [cost]” means “[Cost]: Attach this Equipment to target creature you control. Equip only as a sorcery.”\n" +
                        "----------------------"
        );

        keywords.add(
                "Scry\n" +
                        "To scry N, look at the top N cards of your library, then put any number on the bottom of your library and the rest on top in any order.\n" +
                        "----------------------"
        );

        keywords.add(
                "Cycling\n" +
                        "“Cycling [cost]” means “[Cost], Discard this card: Draw a card.”\n" +
                        "----------------------"
        );

        // there are more

        ruleBookMap.put("Keywords", keywords);

        // 3) Create and set the adapter using SimpleExpandableListAdapter
        ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                createGroupList(ruleSections),
                android.R.layout.simple_expandable_list_item_1,  // layout for group (parent) items
                new String[]{"RULE_HEADER"},                    // key in the group map
                new int[]{android.R.id.text1},                  // id of the TextView to put it in
                createChildList(ruleBookMap, ruleSections),
                android.R.layout.simple_list_item_1,            // layout for child items
                new String[]{"RULE_CHILD"},                     // key in the child map
                new int[]{android.R.id.text1}                   // id of the TextView to put it in
        );

        expandableListView.setAdapter(adapter);
    }

    // Helper to build group-level data
    private List<Map<String, String>> createGroupList(List<String> sectionTitles) {
        List<Map<String, String>> groupList = new ArrayList<>();
        for (String title : sectionTitles) {
            Map<String, String> map = new HashMap<>();
            map.put("RULE_HEADER", title);
            groupList.add(map);
        }
        return groupList;
    }

    // Helper to build child-level data
    private List<List<Map<String, String>>> createChildList(HashMap<String, List<String>> ruleMap,
                                                            List<String> sectionTitles) {
        List<List<Map<String, String>>> childList = new ArrayList<>();
        for (String section : sectionTitles) {
            List<Map<String, String>> childItems = new ArrayList<>();
            List<String> items = ruleMap.get(section);
            if (items != null) {
                for (String childText : items) {
                    Map<String, String> childMap = new HashMap<>();
                    childMap.put("RULE_CHILD", childText);
                    childItems.add(childMap);
                }
            }
            childList.add(childItems);
        }
        return childList;
    }
}
